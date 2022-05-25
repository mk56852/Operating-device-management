package com.example.ODM.Configuration.JobConfiguration;


import org.apache.commons.io.IOUtils;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;


public class StaxEventItemReaderThreadSafe<Record> implements ItemReader<Record>, ItemStream, InitializingBean, ResourceAwareItemReaderItemStream<Record> {

    private StaxEventItemReader<Record> reader = new StaxEventItemReader<>();

    @Override
    public void setResource(Resource resource) {
        if(resource==null){
            reader.setResource(new InputStreamResource(IOUtils.toInputStream("")));
        } else {
            reader.setResource(resource);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        reader.afterPropertiesSet();
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        reader.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        reader.update(executionContext);
    }

    @Override
    public void close() {
        reader.close();
    }

    @Override
    public synchronized Record read() throws UnexpectedInputException, ParseException, Exception {
        return reader.read();
    }

    @Override
    public boolean equals(Object obj) {
        return reader.equals(obj);
    }

    @Override
    public int hashCode() {
        return reader.hashCode();
    }

    public boolean isSaveState() {
        return reader.isSaveState();
    }

    public void setStrict(boolean strict) {
        reader.setStrict(strict);
    }

    public void setCurrentItemCount(int count) {
        reader.setCurrentItemCount(count);
    }

    public void setFragmentRootElementName(String fragmentRootElementName) {
        reader.setFragmentRootElementName(fragmentRootElementName);
    }

    public void setMaxItemCount(int count) {
        reader.setMaxItemCount(count);
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        reader.setUnmarshaller(unmarshaller);
    }

    public void setName(String name) {
        reader.setName(name);
    }


    public void setSaveState(boolean saveState) {
        reader.setSaveState(saveState);
    }

    @Override
    public String toString() {
        return reader.toString();
    }

}