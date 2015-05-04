Digital Display Device Manager(DDDM)
===================================
A browser based digital display device manager for Raspberry Pi based implementations. DDDM can be periodically updated through a remote repository.

Prerequisites
--------------
* Raspberry Pi
* Python 2.7

Folder Structure
-----------------
* **KernelRunner** : A configurable sequence runner which executes resources(URLs, webcontent folders and html pages).
* **Content** : Local webcontent store.

Configurations
------------------

Change relevant information in the respective `digital_display.xml` file.

###### Change the browser.
* Name: Name of the browser handler class(currently supports 'epiphany', 'midori', or 'default').
* Path: Path and/or command to execute webbrowser.
* Port: Port for the webapp.
```xml
    <WebBrowser>
      <Name>midori</Name>
      <Path>midori</Path>
      <Port>8000</Port>
    </WebBrowser>
```
###### Change the sequence.
* @type: Resource type handler class(currently supports 'url', 'folder', and 'page').
* @time: Delay in human readable time format(example: '1h 2m 3s', '30m', '5s').
* @path: path for the resource(applicable only for folder/ page types).
* @url: url for the content page(applicable only for url type).
```xml
    <DisplaySequence>
      <Resource type="folder" time="15s" path="Folder_Sample1" />
      <Resource type="page" time="1h" path="Folder_Sample2/index.html" />
      <Resource type="url" time="30m" url="http://www.wso2.com" />
      <Resource type="folder" time="15s" path="Folder_Sample2" />
    </DisplaySequence>
```
###### Change update policy for KernelRunner and/or Content
* PollingInterval: Delay in human readable time format(example: '1h 2m 3s', '30m', '5s').
* Repository: Name : Root folder of the repository.
* Repository: Url : URL to the remote repository.
* Repository: VCSHandler : Version Control System handler class(svn or git).
```xml
    <UpdatePolicy>
      <Kernel>
        <PollingInterval>30s</PollingInterval>
        <Repository>
          <Name>dd-kernel</Name>
          <Url>path/to/your/dd-kernel.git</Url>
          <VCSHandler>git</VCSHandler>
        </Repository>
      </Kernel>
      <Content>
        <PollingInterval>30s</PollingInterval>
        <Repository>
          <Name>dd-webcontent</Name>
          <Url>path/to/your/dd-webcontent.git</Url>
          <VCSHandler>git</VCSHandler>
        </Repository>
      </Content>
    </UpdatePolicy>
```

Run
------------
Server can be run through a bash terminal. Navigate into KernelRunner folder and type the following;

    python wso2server.py
