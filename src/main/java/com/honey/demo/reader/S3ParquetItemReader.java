package com.honey.demo.reader;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.springframework.batch.item.ItemReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.parquet.hadoop.util.HadoopInputFile;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
@Component
public class S3ParquetItemReader implements ItemReader<Group> {

    @Value("#{jobParameters['source-bucket'] ?: '${source.bucket}'}")
    private String bucketName;

    @Value("#{jobParameters['source-bucket-prefix'] ?: '${source.bucket.prefix}'}")
    private String prefix;

    @Value("#{jobParameters['source-bucket-object'] ?: '${source.bucket.object}'}")
    private String objectName;

    private ParquetReader<Group> reader;
    private S3Client s3;

    public S3ParquetItemReader(){

    }
    private void initializeReader() throws IOException {
        if (reader == null) {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
            System.out.println("===>" +bucketName + prefix + objectName + "<====");
            S3Object s3Object = s3Client.getObject(bucketName, prefix + objectName);



            S3ObjectInputStream inputStream = s3Object.getObjectContent();

            java.nio.file.Path tempDir = Files.createTempDirectory("s3-download");
            File tempFile = new File(tempDir.toFile(), "data.parquet");

            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Using HadoopInputFile to create a ParquetReader
            Path path = new Path(tempFile.getPath());
            HadoopInputFile inputFile = HadoopInputFile.fromPath(path, new Configuration());

            reader = ParquetReader.builder(new GroupReadSupport(), inputFile.getPath()).build();
        }
    }

    @Override
    public Group read() throws Exception {
        if (reader == null) {
            initializeReader();
        }

        return reader.read();
    }
}