Web crawler readme
========================Required .jar files======================================================
Please ensure the following jar files exist within the ./src/lib/ directory:
	jtidy-r938.jar. Google search the jar name. Download it from sourceforge (second link).
	jsoup-1.9.1.jar. Google search the jar name. Download it from Maven, second link.
	hadoop-core-1.0.3.jar. Google search the jar name. Download it from Maven, second link.
	junit-4.12.jar. Google search "junit download." On the first link, junit.org, click "Download and Install" near the middle of the page.
	rabin-hash-function-2.0.jar. Google search the jar name. Download it from sourceforge (first link).

=======================How to run the crawler from the command line==============================
1. In this directory, run "ant"
2. Run "java -cp crawler.jar crawler.CrawlerThreaded <pageCount> <testOut dir> <imgsOut dir>"
	pageCount specifies approximately how many pages are crawled.
	outDir is the output directory. Each worker appends workerID + ".txt".
	imgsOutDir is the output directory of Image URLs. Each worker appends workerID + ".txt"

=====================================Test cases==================================================
To run test cases through command line, please
1. Run "ant" within this directory to produce the project jar file.
2. Run "ant test" within this directory to compile and run the test cases. The results will print to console.

==================================How to run the crawler through AWS EC2=========================
1. Create an AmazonAWS instance through the AmazonEC2 console. Take note of the IP address.
2. Run "ant" in this directory to compile the project into a jar file.
3. When the instance is live, scp the jar file to your instance. Place it in a directory named lib in root. Provided you used the included build.xml, you do not need to transfer the other jars.
4. ssh to your instance.
5. Use mkdir to produce folder "out." Otherwise the root directory will become too cluttered
6. Follow step 2 in the crawler command line instructions above.
7. Transfer the output files to persistent storage like S3.
8. Terminate the instance through the AmazonEC2 console to avoid extraneous charges.