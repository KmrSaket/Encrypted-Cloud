# Encrypted-Cloud
This is an Android application that encrypts images before uploading it to the Cloud storage. 


## Why this project?
Apart from several risks of cloud storage, data privacy and data leakage are of main concerns for the users. User's images stored in cloud is always vulnerable of getting hijacked. But what if we encrypt them before uploading it to cloud.
Yup! sounds interesting?
So in this Android application I have implemented the same concept where images are stored to cloud after the bitmap pixel manipulation. It uses the encryption algorithm based on Rubik's cube principle, proposed in a <a href="https://drive.google.com/file/d/1eu9amz3MNaEH7ifmQtIPekiVcqyFcXbK/view">Research Paper</a> published at Laval University, Canada in 2011.

<a href="https://drive.google.com/file/d/1LPEc36TpggjmICnPz6uzfYsHeIxn1VXG/view">Download the app</a>

## How does this works?
Encryption Phase :-
Firstly the image browsed by user is converted into the pixel matrix. This matrix contains the ARGB value of every pixels in the image. This matrix is then used for manipulating the pixel information of the image and a new image with the same dimensions is created.
In the process of pixel manipulation two arrays are created (whose size is equal to the dimensions of the image in pixel) by the application itself. One array is of size of width of image in pixels and other array is of size of height of image in pixels. And the value of the elements is randomly generated between the range 0 - 2²⁴. These two arrays are the randomly generated keys that is stored to user's phone internal storage.

Decryption Phase :-
In this also the encrypted image is converted into pixel matrix. And then the two array is extracted from the key file. After that the pixel matrix is decrypted and original matrix is derived. Lastly, this matrix is converted back to bitmap.


## Key features
* Encrypt your image with a random generated key.
* Images get encrypted and uploaded automatically.
* The key for the image is stored in your phone internal storage root/Encrypted Cloud/keys.


## How to use
<ol>
<li>To use this application the user needs to sign in first using their Google account. This login is used to create a cloud storage account for the user.</li>
(GIF)

<li>Select any one of the features provided in the home page.</li>
(PIC)

<li>For encryption select any image from "BROWSE" button. After the image shows up, click on "UPLOAD" button to encrypt and store the image.</li>
(GIF)
Note: If user selects cloud option then the encrypted image gets stored into cloud storage of the user. And if user selects local option then it gets stored in the user's phone storage.
phone_internal_storage/Encryption Cloud/local/images/

<li>For decryption just make sure that the key and image are present in the system generated location.</li>
Note: If user wants to decrypt image from its cloud storage then the key for the image should be present in phone_internal_storage/Encryption Cloud/Key/. And if user is decrypting image from local then key should be present in phone_internal_storage/Encryption Cloud/local/key/ and images should be present in phone_internal_storage/Encryption Cloud/local images/
</ol>