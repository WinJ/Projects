A distributed web crawler based on master-slave Redis cluster to download images from websites. Multiple Redis clients across different nodes can access the master and do the job. Master-slave replication is also enabled for Redis to improve the availability.

The program can be easily modified to do tasks other than image-downloading.

1. WebCrawlerRedis.java is to crawl web pages and put image URLs in the list in Redis. Multiple Redis clients can be used to access master to speedup the process.

2. ImageDownloader.java is to download images based on the image URL list. Again multiple clients across nodes can be used to speedup.

To do:
Right now only master can be used to do real job, slave is for replication and read only. Try to use Redis Cluster instead of master-slave model, so each node can be used to crawl and download. Replication and sharding needs to be done for this.

