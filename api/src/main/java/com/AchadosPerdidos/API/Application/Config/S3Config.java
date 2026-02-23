package com.AchadosPerdidos.API.Application.Config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Getter
@Configuration
public class S3Config {

    @Value("${aws.s3.access-key:default-access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key:default-secret-key}")
    private String secretKey;

    @Value("${aws.s3.bucket-name:}")
    private String bucketName;

    @Value("${aws.s3.region:us-east-1}")
    private String region;

    @Value("${aws.s3.endpoint-url:}")
    private String endpointUrl;

    @Bean
    @Primary
    public S3Client s3Client() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        var builder = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of(region));

        if (endpointUrl != null && !endpointUrl.trim().isEmpty()) {
            builder.endpointOverride(java.net.URI.create(endpointUrl));
        }

        return builder.build();
    }

    @Bean
    public String s3BucketName() {
        return bucketName;
    }

    @Bean
    public S3Presigner s3Presigner() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        var builder = S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of(region));

        if (endpointUrl != null && !endpointUrl.trim().isEmpty()) {
            builder.endpointOverride(java.net.URI.create(endpointUrl));
        }

        return builder.build();
    }
}