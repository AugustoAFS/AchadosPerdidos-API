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

    @Value("${AWS_S3_ACCESS_KEY:default-access-key}")
    private String AccessKey;

    @Value("${AWS_S3_SECRET_KEY:default-secret-key}")
    private String SecretKey;

    @Value("${AWS_S3_BUCKET:}")
    private String BucketName;

    @Value("${AWS_S3_REGION:us-east-1}")
    private String region;

    @Value("${AWS_S3_ENDPOINT_URL:}")
    private String EndpointUrl;

    @Bean
    @Primary
    public S3Client s3Client() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(AccessKey, SecretKey);

        var builder = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of(region));

        if (EndpointUrl != null && !EndpointUrl.trim().isEmpty()) {
            builder.endpointOverride(java.net.URI.create(EndpointUrl));
        }

        return builder.build();
    }

    @Bean
    public String s3BucketName() {
        return BucketName;
    }

    @Bean
    public S3Presigner s3Presigner() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(AccessKey, SecretKey);

        var builder = S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of(region));

        if (EndpointUrl != null && !EndpointUrl.trim().isEmpty()) {
            builder.endpointOverride(java.net.URI.create(EndpointUrl));
        }

        return builder.build();
    }
}