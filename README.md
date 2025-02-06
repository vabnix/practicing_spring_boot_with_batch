# Spring Boot with Batch 5.0

This code processes the CSV file with 100,000 record in 15s267ms

### Changes Added
- Updated the chunk size from 100 to 1000
- Added Parallel Processing using TaskExecutor
- ```declarative
  executor.setCorePoolSize(4);
  executor.setMaxPoolSize(8);
  executor.setQueueCapacity(20);

