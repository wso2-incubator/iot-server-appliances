#### Folder Structure

The folders are structured to give a **step-by-step** guide to the **Hackathon** milestones.

* **Step 1** - *Arduino Sketch* plus *schematics diagram* for controlling the **SenseBot** via Serial Communication 
* **Step 2** - *Arduino Sketch* for controlling the **SenseBot** via Wifi Communication (Wifi-Sheild required)
* **Step 3** - The JAX-RS implementation that exposes the APIs related to SenseBot (This includes the bam configurations and a jaggery app to view the statistics of the sensor data from SenseBot)
* **Step 4** - The *schematics* and *sketches* for attaching/testing each sensor (DHT Temperature, PIR(Motion), LDR(Light) & Sonar)

*(Todo - merge Step 4 (to retreive sensor data) and step 1 and make the sensebot to move with predefined commands while gathering the data)*
* **Step 5** - Arduino Sketch to retreive & push **temperature** sensor data **(alone)** to WSO2 BAM from SenseBot - ***without*** motor controlling
* **Step 6** - Arduino Sketch to retreive & push **ALL** sensor data to WSO2 BAM from SenseBot - ***without*** motor controlling
* **Step 7** - Arduino Sketch to retreive & push **ALL** sensor data to WSO2 BAM from SenseBot - ***WITH*** motor controlling enabled

