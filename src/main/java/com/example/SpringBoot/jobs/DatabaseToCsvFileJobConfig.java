package com.example.SpringBoot.jobs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.example.SpringBoot.model.StudentDTO;


@Configuration
public class DatabaseToCsvFileJobConfig {

    private static final String PROPERTY_CSV_EXPORT_FILE_PATH = "D:/csv/OATSFile.csv";
    
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    
    private class InMemoryStudentReader implements ItemReader<StudentDTO> {	
		private int nextStudentIndex;
	    private List<StudentDTO> studentData;
	 
	    InMemoryStudentReader() {
	        initialize();
	    }
	 
	    private void initialize() {
	        StudentDTO tony = new StudentDTO("1", "1", "1");
	        
	        StudentDTO nick = new StudentDTO("2", "2", "2");
	        
	        StudentDTO ian = new StudentDTO("3", "3", "3");
	        
	        studentData = Collections.unmodifiableList(Arrays.asList(tony, nick, ian));
	        nextStudentIndex = 0;
	    }
	 
	    @Override
	    public StudentDTO read() throws Exception {
	        StudentDTO nextStudent = null;
	 
	        if (nextStudentIndex < studentData.size()) {
	            nextStudent = studentData.get(nextStudentIndex);
	            nextStudentIndex++;
	        }
	 
	        return nextStudent;
	    }   	
	};		

    @Bean
    ItemReader<StudentDTO> databaseCsvItemReader() {
    	ItemReader<StudentDTO> itemReader = new InMemoryStudentReader();    		    
        return itemReader; 
    }


    @Bean
    ItemProcessor<StudentDTO, StudentDTO> databaseCsvItemProcessor() {
        return new ItemProcessor<StudentDTO, StudentDTO>(){

			@Override
			public StudentDTO process(StudentDTO arg0) throws Exception {
				// TODO Auto-generated method stub
				return arg0;
			}
        	
        };
    }

    @Bean   
    ItemWriter<StudentDTO> databaseCsvItemWriter() {
        FlatFileItemWriter<StudentDTO> csvFileWriter = new FlatFileItemWriter<>();        
        csvFileWriter.setResource(new FileSystemResource(PROPERTY_CSV_EXPORT_FILE_PATH));

        LineAggregator<StudentDTO> lineAggregator = createStudentLineAggregator();
        csvFileWriter.setLineAggregator(lineAggregator);

        return csvFileWriter;
    }

    private LineAggregator<StudentDTO> createStudentLineAggregator() {
        DelimitedLineAggregator<StudentDTO> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");

        FieldExtractor<StudentDTO> fieldExtractor = createStudentFieldExtractor();
        lineAggregator.setFieldExtractor(fieldExtractor);

        return lineAggregator;
    }

    private FieldExtractor<StudentDTO> createStudentFieldExtractor() {
        BeanWrapperFieldExtractor<StudentDTO> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[] {"accountNumber", "shortName", "office"});
        return extractor;
    }

    @Bean
    Step databaseToCsvFileStep(ItemReader<StudentDTO> databaseCsvItemReader,
                               ItemProcessor<StudentDTO, StudentDTO> databaseCsvItemProcessor,
                               ItemWriter<StudentDTO> databaseCsvItemWriter,
                               StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("databaseToCsvFileStep")
                .<StudentDTO, StudentDTO>chunk(1)
                .reader(databaseCsvItemReader)
                .processor(databaseCsvItemProcessor)
                .writer(databaseCsvItemWriter)
                .build();
    }

    @Bean
    Job databaseToCsvFileJob(@Qualifier("databaseToCsvFileStep") Step csvStudentStep) {
        return jobBuilderFactory.get("databaseToCsvFileJob")
                .incrementer(new RunIdIncrementer())
                .flow(csvStudentStep)
                .end()
                .build();
    }
}