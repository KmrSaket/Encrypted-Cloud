# Encrypted-Cloud
This is an Android application that encrypts images before uploading it to the Cloud storage. 

<a href="https://drive.google.com/file/d/1LPEc36TpggjmICnPz6uzfYsHeIxn1VXG/view">Download the app</a>

## Why this project?
Apart from several risks of cloud storage, data privacy and data leakage are of main concerns for the users. User's images stored in cloud is always vulnerable of getting hijacked. </br> But what if we encrypt them before uploading it to cloud.
Yup! sounds interesting? </br>
So in this Android application I have implemented the same concept where images are stored to cloud after the bitmap pixel manipulation. It uses the encryption algorithm based on Rubik's cube principle, proposed in a <a href="https://drive.google.com/file/d/1eu9amz3MNaEH7ifmQtIPekiVcqyFcXbK/view">Research Paper</a> published at Laval University, Canada in 2011.


## How does this works?
* **Encryption phase :-**
Firstly, the image browsed by user is converted into the pixel matrix. This matrix contains the ARGB value of every pixels in the image. </br> This matrix is then used for manipulating the pixel information of the image and a new image with the same dimensions is created.
In the process of pixel manipulation two arrays are created (whose size is equal to the dimensions of the image in pixel). One array is of size of width of image in pixels and other array is of size of height of image in pixels. And the value of the elements is randomly generated between the range 0 to 2²⁴-1. These two arrays are the randomly generated keys that is stored to user's phone internal storage. </br>
**Note :- Encrypted image and the key file are given the same name. So during the Decryption also, the name of the image should be same as that of the key file, otherwise it will show key not found error.**

* **Decryption phase :-**
In this phase also the encrypted image is converted into pixel matrix. And then the two array is extracted from the key file. After that the pixel matrix is decrypted and original matrix is derived. Finally, this matrix is converted back to bitmap image.


## Key features
* Encrypt your image with a random generated key.
* Images get encrypted and uploaded automatically.
* The keyspace is very large and depends on the dimensions of the image.
* The key and the image is stored in your phone-internal-storage-root/Encrypted Cloud/.


## How to use
<ol>
<li>To use this application the user needs to sign in first using their Google account. This login is used to create a cloud storage account for the user. </br> 
<a href="url"><img src="https://github.com/KmrSaket/Encrypted-Cloud/blob/master/Readme%20Resources/Login.png" height="400" ></a>
</li>

<li>Select any one of the features provided in the home page. </br>
<a href="url"><img src="https://github.com/KmrSaket/Encrypted-Cloud/blob/master/Readme%20Resources/Home.png" height="400" ></a>

* **Store to Cloud** : Select any image from "BROWSE" button. After the image shows up, click on "UPLOAD" button to encrypt and store the image. Encrypted image and the key file is stored as follows. </br> **Note :- The name of encrypted image and the name of key file are same. This is the necessary condition for decryption.** </br>
<a href="url"><img src="https://github.com/KmrSaket/Encrypted-Cloud/blob/master/Readme%20Resources/Enc_EncryptionInprogress2.png" height="400" ></a>

* **Retrieve from Cloud** : Here decryption takes place automatically. For every image uploaded on the cloud, the application looks for the key in the Phone directory. If the key is not present or if the key file is renamed then it shows key not found error. </br>
<a href="url"><img src="https://github.com/KmrSaket/Encrypted-Cloud/blob/master/Readme%20Resources/Dec_DecryptedImages.png" height="400" ></a>

* **Store to Local** : Select any image from "BROWSE" button. After the image shows up, click on "UPLOAD" button to encrypt and store the image. Encrypted image and the key file is stored as follows. </br> **Note :- The name of encrypted image and the name of key file are same. This is the necessary condition for decryption.** </br>

* **Retrieve from Local** : In this also decryption takes place automatically. For every image encrypted and stored in local storage, the application looks for the key in the Phone directory. If the key is not present or if the key file is renamed then it shows key not found error. </br>
</li>
<li>User can sign out anytime from dashboard. </br>
</li>
</ol>

## Other ScreenShots
<a href="url"><img src="https://github.com/KmrSaket/Encrypted-Cloud/blob/master/Readme%20Resources/Enc_EncryptionCompleted.png" height="400" ></a>
<a href="url"><img src="https://github.com/KmrSaket/Encrypted-Cloud/blob/master/Readme%20Resources/KeysStorage.png" height="400" ></a>
<a href="url"><img src="https://github.com/KmrSaket/Encrypted-Cloud/blob/master/Readme%20Resources/KeyGenerated.png" height="400" ></a>
