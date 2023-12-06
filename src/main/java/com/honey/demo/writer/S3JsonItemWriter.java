package com.honey.demo.writer;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class S3JsonItemWriter implements ItemWriter<String> {

    private final String bucketName;
    private final String destinationPrefix;

    public S3JsonItemWriter(String bucketName, String destinationPrefix) {
        this.bucketName = bucketName;
        this.destinationPrefix = destinationPrefix;
    }

    @Override
    public void write(Chunk<? extends String> items) throws Exception {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().build();
        String combinedJson = String.join("\n", items);

        // Generate a unique file name. You can customize it as per your requirement.
        String fileName = destinationPrefix + "/output_" + System.currentTimeMillis() + ".json";

        byte[] contentAsBytes = combinedJson.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream contentsAsStream = new ByteArrayInputStream(contentAsBytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentAsBytes.length);

        s3Client.putObject(new PutObjectRequest(bucketName, fileName, contentsAsStream, metadata));
    }
}
