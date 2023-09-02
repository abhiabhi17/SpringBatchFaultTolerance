package com.example.springbatchfaulttolernance.listener;

import com.example.springbatchfaulttolernance.entity.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;


/* Skip Listener is an interface which listens all the exceptions where occured in item reader or item writer or processor  */
public class StepSkipListener implements SkipListener<Customer,Number> {

    Logger logger= LoggerFactory.getLogger(StepSkipListener.class);

    @Override
    public void onSkipInRead(Throwable throwable) {
       logger.info("A failure on read {} ",throwable.getMessage());
    }

    @Override
    public void onSkipInWrite(Number item, Throwable throwable) {
        logger.info("A Failure on write {} , {}",throwable.getMessage(),item);

    }

    @SneakyThrows
    @Override
    public void onSkipInProcess(Customer customer, Throwable throwable) {
        logger.info("Item {} was skipped due to exception {}",
                new ObjectMapper().writeValueAsString(customer),
                throwable.getMessage());

    }
}
