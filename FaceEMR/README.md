# FaceEMR
## Emotion Recognition from Facial Expression 

The project aims to train a model using tensorflow for facial emotion detection and used the trained model 
as predictor in android facial expression recongnition app.

The model is trained using  tensorflow python framework and used in android application where the basic langauge is java. 

Basically tensorflow provides a c++ api, that can be used in android application. The trained model by python langauge can be integrated with android project  after inclduing tensorflow c++ framework dependencies and using native interface the model can be loaded and called in java class. This is the whole thing. 

The total work of this project is divided into two parts 
1) Devlop  a model in tensoflow using python langauge
  
   **KaggleFaceEmotionRecognition** folder contains the work 
2) Devlop an android appication for facial expression recongtion 
  
   **FaceEMR** folder contains the work 


### Part 1. Facial Expression Recongition Model developed in Tensorflow 

In this work , I have used a simple Convolutional Neural Network Architecture to train a facial expression dataset.

**1. DataSet:** The dataset is collected from Facial xpression recognition challenge  in kaggle
The challenge link https://www.kaggle.com/c/challenges-in-representation-learning-facial-expression-recognition-challenge/

The data consists of 48x48 pixel grayscale images of faces.The dataset contains facial expression  of seven categories (0=Angry, 1=Disgust, 2=Fear, 3=Happy, 4=Sad, 5=Surprise, 6=Neutral

**2. Model:** 

    In this work I have used the below CNN model 
    
      input_image->conv2d->pooling->conv2d->pooling->conv2d->pooling->dropout->softmax
      
   The code fragment
   
    ```
    x_image = tf.reshape(x, [-1, 48, 48, 1])
        #48*48*1
        conv1 = tf.layers.conv2d(x_image, 64, 3, 1, 'same', activation=tf.nn.relu)
        #48*48*64
        pool1 = tf.layers.max_pooling2d(conv1, 2, 2, 'same')
        #24*24*64
        conv2 = tf.layers.conv2d(pool1, 128, 3, 1, 'same', activation=tf.nn.relu)
        #24*24*128
        pool2 = tf.layers.max_pooling2d(conv2, 2, 2, 'same')
        #12*12*128
        conv3 = tf.layers.conv2d(pool2, 256, 3, 1, 'same', activation=tf.nn.relu)
        #12*12*256
        pool3 = tf.layers.max_pooling2d(conv3, 2, 2, 'same')
        #6*6*256
        flatten = tf.reshape(pool3, [-1, 6*6*256])
        fc = tf.layers.dense(flatten, 1536, activation=tf.nn.relu)
        dropout = tf.nn.dropout(fc, keep_prob)
        logits = tf.layers.dense(dropout, 7)
        outputs = tf.nn.softmax(logits, name=output_node_name)
        ```
  
**3. Result:** I have used 5000 iterations with batch size 100 and restore the model in protocal buffer file

### Part 2.  Facial Expression Recongition Application in Android

I have used Android Studio for this application. 

Integrating tensorflow dependency in android is really a tedious thing. the good news is that the latest news that android studio manages all dependencis related to tensorflow after adding the dependencies in *build.gradle(Module:app)* file 

```
dependencies {
    compile 'org.tensorflow:tensorflow-android:+' 
}

```

The final dependency part looks like 

```
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'com.android.support:appcompat-v7:26.1.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.0'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.1'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.1'
    compile 'org.tensorflow:tensorflow-android:+'
}
```



**1. Designing the UI Components**

Home Screen look like this
![Home Screen ](/images/home.png)

After taking a picture 
![Home Screen ](/images/camera.png)

And the final result 
![Home Screen ](/images/detect.png)



**2. Interacting with the Tensorflow Native Api**

The *org.tensorflow.contrib.android.TensorFlowInferenceInterface* handles all necessary operation to interact with native api. See more details in https://github.com/tensorflow/tensorflow/tree/master/tensorflow/contrib/android

**3. Finalizing the work** 

There are lots of options for improvement.
 1. Adding some cool features and increasing model performance using another models
 2. Tuning the hyperparameters of used cnn model 






