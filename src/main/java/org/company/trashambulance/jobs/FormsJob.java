package org.company.trashambulance.jobs;

import org.company.trashambulance.services.FormService;
import org.company.trashambulance.services.ForwardDataService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class FormsJob extends QuartzJobBean {

    private static final Logger logger = LoggerFactory.getLogger(FormsJob.class);

    @Autowired
    private FormService formService;
    @Autowired
    private ForwardDataService forwardDataService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        logger.info("Executing FormsJob task at {}", context.getFireTime());
        formService.deleteExpiredForms();
        forwardDataService.deleteExpiredForms();
    }
}
