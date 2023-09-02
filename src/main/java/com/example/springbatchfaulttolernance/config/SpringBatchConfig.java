package com.example.springbatchfaulttolernance.config;

import com.example.springbatchfaulttolernance.entity.Customer;
import com.example.springbatchfaulttolernance.listener.StepSkipListener;
import com.example.springbatchfaulttolernance.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private CustomerItemWriter customerItemWriter;
    private CustomerRepository customerRepository;

    /*  Job have Steps
         Steps have Iteam Reader- Item Processor-Item Writer
     */
    @Bean
    public Job runJob()
    {
        return jobBuilderFactory.get("importCustomers")
                .flow(step1()).end().build();
    }

    /* In Step We have to give reader () bean, writer bean which is of customerItemWriter Which implements ItemWriter
    *  Fault Tolerance is like exception handling , if any exceptions occured during reading or wrting csv file ,process of writing to database should not stop
    * skip method is for to skip  that special kind of exception  like number format exception
    * Listner class is providing bean of skiplisten which implements skip listeenr
    * which listens where the excpetion occurd whter it is in itemreder or item writer or item processor */
    @Bean
    public Step step1()
    {
        return stepBuilderFactory.get("csv-step")
                .<Customer,Customer>chunk(5)
                .reader(reader())
                .processor(processor())
                .writer(customerItemWriter)
                .faultTolerant()
                //.skipLimit(100)
               // .skip(NumberFormatException.class)
               // .noSkip(IllegalArgumentException.class)
                .listener(skipListener())
                .skipPolicy(skipPolicy())
                .build();
    }

    /* Skip Listener is an interface which listens all the exceptions where occured in item reader or item writer or processor  */

    @Bean
    public  Object skipListener() {
        return new StepSkipListener();
    }


    /* Skip Policy  is an interface is used to write our custom skip exceptions just like .skip(NumberformatException.class) */
    @Bean
    public  SkipPolicy skipPolicy() {
        return new ExceptionSkipPolicy();
    }


    /* Item Reader is used to read the  the data from csv file
    * skip lines is used to skip the header lien of csv file
    * line mapper bean is used to read the data line by line  */
    @Bean
    public FlatFileItemReader reader() {
        FlatFileItemReader<Customer> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
        flatFileItemReader.setName("CSV-Reader");
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(lineMapper());
        return flatFileItemReader;
    }

    /* Item Reader is used to read the  the data from csv file
     * skip lines is used to skip the header lien of csv file
     * line mapper bean is used to read the data line by line  */
    @Bean
    public  LineMapper<Customer> lineMapper() {
        DefaultLineMapper lineMapper=new DefaultLineMapper();

        DelimitedLineTokenizer lineTokenizer=new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob", "age");


        /* BeanWrapperField is used to map the csv file oject to customer object  */
        BeanWrapperFieldSetMapper<Customer> fieldSetMapper=new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;

    }

    @Bean
    public CustomerProcessor processor() {
        return  new CustomerProcessor();
    }
    @Bean
    public RepositoryItemWriter<Customer> writer() {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }
}
