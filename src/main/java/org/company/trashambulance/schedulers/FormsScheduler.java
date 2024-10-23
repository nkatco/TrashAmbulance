package org.company.trashambulance.schedulers;

import jakarta.annotation.PostConstruct;
import org.company.trashambulance.jobs.FormsJob;
import org.company.trashambulance.services.FormService;
import org.company.trashambulance.services.ForwardDataService;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FormsScheduler {
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private FormService formService;
    @Autowired
    private ForwardDataService forwardDataService;

    @PostConstruct
    public void init() throws SchedulerException {
        if(!createJobFormsIfNotExists()) {
            System.out.println("Schedule task FormsScheduler");

            formService.deleteExpiredForms();
            forwardDataService.deleteExpiredForms();
        }
    }
    public boolean createJobFormsIfNotExists() throws SchedulerException {
        JobKey jobKey = new JobKey("FORMS", "users");

        if (!scheduler.checkExists(jobKey)) {
            System.out.println("Create Job Forms If Not Exists");
            JobDetail jobDetail = JobBuilder.newJob(FormsJob.class)
                    .withIdentity(jobKey)
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .forJob(jobDetail)
                    .withIdentity("FORMS" + "Trigger", "users")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 * ? * *"))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            return false;
        }
        return true;
    }
}
