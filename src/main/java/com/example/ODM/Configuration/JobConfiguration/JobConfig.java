package com.example.ODM.Configuration.JobConfiguration;

import com.example.ODM.Configuration.ShipmentFileConfiguration.ShipmentFileConfig;
import com.example.ODM.Domain.Meter.Meter;

import com.example.ODM.Util.M2mMappingEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;

import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.xml.StaxEventItemReader;

import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;



@Configuration
@EnableBatchProcessing
@Slf4j
@AllArgsConstructor
public class JobConfig {

    @Autowired
    public JobBuilderFactory jobBuilderFactory ;
    @Autowired
    public StepBuilderFactory stepBuilderFactory ;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JobListener jobListener ;
    @Autowired
    private ShipmentFileConfig shipmentFileConfig;
    @Autowired
    private ItemProcessor<Meter,M2mMappingEntity> processor ;




    /****** Reader  Thread Safe *******/


    @StepScope
    @Bean
    public StaxEventItemReaderThreadSafe reader(

            @Value( "#{jobParameters['path']}" )
                    String inputFile )
    {

        StaxEventItemReaderThreadSafe  reader = new StaxEventItemReaderThreadSafe ();
        reader.setResource(new FileSystemResource(shipmentFileConfig.getOdmProcessingFile()+"/"+inputFile));
        reader.setFragmentRootElementName("Meter");
        reader.setUnmarshaller(new Jaxb2Marshaller(){
            {
                setClassesToBeBound(Meter.class);
            }
        });
        return reader;
    }

    /****** Simple Reader *******/

    @StepScope
    @Bean
    public StaxEventItemReader simpleReader(

            @Value( "#{jobParameters['path']}" )
                    String inputFile )
    {

        StaxEventItemReader reader = new StaxEventItemReader();
        reader.setResource(new FileSystemResource(shipmentFileConfig.getOdmProcessingFile()+"/"+inputFile));
        reader.setFragmentRootElementName("Meter");
        reader.setUnmarshaller(new Jaxb2Marshaller(){
            {
                setClassesToBeBound(Meter.class);
            }
        });
        return reader;
    }


    /******** JDBC ITEM WRITER  ********/


    @StepScope
    @Bean
    public JdbcBatchItemWriter writer (@Value( "#{jobParameters['shipmentFileId']}" )  String shipmentFileId  ,
                                       @Value( "#{jobParameters['meterType']}" ) String meterType ){
        if(meterType == "GAZ")
        return new JdbcBatchItemWriterBuilder<Meter>()
                .dataSource(this.dataSource)
                .sql("insert into meter_gaz ( device_name, logical_device_name, type, key , status ,model_version , shipment_file_id , amr_router,communication_method,hardware_version,box,firmware_version, is_connected   )" +
                        " values (  :deviceName, :logicalDeviceName, :type, :key ,"+ 0 +" ,:modelVersion ,"+shipmentFileId+", :amrRouter,:communicationMethod,:hardwareVersion,:box,:firmwareVersion, FALSE ) ")

                .beanMapped()
                .build();
        else
            return new JdbcBatchItemWriterBuilder<Meter>()
                    .dataSource(this.dataSource)
                    .sql("insert into meter_elec ( device_name, logical_device_name, type, key , status ,model_version , shipment_file_id , amr_router,communication_method,hardware_version,box,firmware_version, is_connected)" +
                            " values (  :deviceName, :logicalDeviceName, :type, :key ,"+ 0 +" ,:modelVersion ,"+shipmentFileId+", :amrRouter,:communicationMethod,:hardwareVersion,:box,:firmwareVersion, FALSE ) ")

                    .beanMapped()
                    .build();

    }


    @StepScope
    @Bean
    StaxEventItemWriter<M2mMappingEntity> xmlItemWriter(@Value( "#{jobParameters['path']}" ) String fileName) {
        StaxEventItemWriter<M2mMappingEntity> xmlFileWriter = new StaxEventItemWriter<>();

        String exportFilePath = shipmentFileConfig.getMappingFileDirectory()+"/Mapping_File_"+fileName ;
        xmlFileWriter.setResource(new FileSystemResource(exportFilePath));

        xmlFileWriter.setRootTagName("MappingList");

        Jaxb2Marshaller meterMarshaller = new Jaxb2Marshaller();
        meterMarshaller.setClassesToBeBound(M2mMappingEntity.class);

        xmlFileWriter.setMarshaller(meterMarshaller);

        return xmlFileWriter;
    }





    /******  Simple Step **********/

    @Bean
    public Step step1() {

        return  stepBuilderFactory.get("step1")
                .<Meter,Meter>chunk(100000)
                .reader(reader(null))
                .writer(writer(null,null))
                .build() ;
    }



    /******  MultiThread Step **********/
    @Bean
    public Step xmlToDbMultiThreadStep() {

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(8);
        taskExecutor.setMaxPoolSize(8);
        taskExecutor.afterPropertiesSet();
        return stepBuilderFactory.get("multi_thread_step")
                .<Meter, Meter>chunk(new SimpleCompletionPolicy(30000))
                .reader(reader(null))
                .writer(writer(null,null))
                .taskExecutor(taskExecutor)
                .build();
    }


    @Bean
    public Step xmlMappingStep() {

        return  stepBuilderFactory.get("step1")
                .<Meter,M2mMappingEntity>chunk(30000)
                .reader(reader(null))
                .processor(processor)
                .writer(xmlItemWriter(null))
                .build() ;
    }

    /********  FLOWS     *********/

    @Bean
    public Flow flow1 () {
        return new FlowBuilder<SimpleFlow>("flow1").start(xmlToDbMultiThreadStep()).build() ; }

    @Bean
    public Flow flow2 () {
        return new FlowBuilder<SimpleFlow>("flow2").start(xmlMappingStep()).build() ; }

    @Bean
    public Flow parallelFlow () {
        return new FlowBuilder<SimpleFlow>("parallelFlow")
                .split(new SimpleAsyncTaskExecutor())
                .add(flow2(),flow1())
                .build() ;
    }


    /********  JOB  ********/

    @Bean
    public Job importMeterJob(){
        return jobBuilderFactory.get("MeterUpload")
                .start(parallelFlow())
                .build()
                .listener(jobListener)
                .build();

    }









}
