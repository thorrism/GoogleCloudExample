# GoogleCloudExample
Example project displaying how to use the Google Cloud Storage API for Java with Android. Displays images and videos the user
has uploaded onto the Google Cloud Storage server.


SETUP (assuming you have a developer's account with Google Play Store):

1) First, setup your account with Google Cloud Storage at: https://cloud.google.com/storage/docs/signup

2) Create your new project at: https://console.developers.google.com/project

3) Enable Google Cloud Storage by going to: https://console.developers.google.com/project select your project and find APIs. 
under APIs find Google Cloud Storage and click "Enable API". Your account is now ready to use the Google Cloud!

4) Now you need your credentials (or key) to enable you to use the API in your Android application (or java application). Right
under API's from the last step is "Credentials". Download your p12 key by clicking "Generate New Key". Save this somewhere you
will remember, because it is very important and DO NOT DISTRIBUTE THIS. It is the key file that enables you to use your information
and access your Google Cloud Storage data.

5) Under the same Credientials tab you will find the email address and project ID needed for your project. Modify the StorageConstants.java
to have "PROJECT_ID_PROPERTY" be the first part of your project's email address. Also, change the application name property to be your
project's name for Google Cloud Storage.

  Email example: 1234567891011-OTHERINFO@developer.gserviceaccount.com
  
  Project ID: 1234567891011
  
You are now ready to use this project as an example for Google Cloud Storage! 
