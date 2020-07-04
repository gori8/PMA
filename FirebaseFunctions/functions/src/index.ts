import * as functions from 'firebase-functions';

// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
// export const helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
'use strict';

const admin = require('firebase-admin');
admin.initializeApp();

/**
 * Triggers when a user gets a new follower and sends a notification.
 *
 * Followers add a flag to `/followers/{followedUid}/{followerUid}`.
 * Users save their device notification tokens to `/users/{followedUid}/notificationTokens/{notificationToken}`.
 */
exports.sendChatNotification = functions.database.ref('/messages/{userOneId}/{userTwoId}/{messageId}')
    .onCreate(async (snap, context) => {
      const userOneId = context.params.userOneId;
      const userTwoId = context.params.userTwoId;
      // If un-follow we exit the function.
      //if (!change.after.val()) {
        //return console.log('User 1', userOneId, 'User 2', userTwoId);
      //}

      functions.logger.log(userOneId);
      functions.logger.log(userTwoId);
      functions.logger.log(snap.val());

      var userToSendNotif;
      var senderId = snap.val().from;

      if(snap.val().from == userOneId){
        userToSendNotif=userTwoId;
      }else{
        userToSendNotif=userOneId;
      }

      var senderName;

      var db = admin.database();
      var ref = db.ref("/Users/"+senderId);

      ref.once("value", function(snapshot) {
        functions.logger.log(snapshot.val());
        senderName=snapshot.val().name;
        var message:any = {
          data: {
            notificationType : "CHAT",
            title: senderName,
            message: snap.val().message
          },
          topic: userToSendNotif
        };

        admin.messaging().send(message)
        .then((response) => {
          // Response is a message ID string.
          console.log('Successfully sent message:', response);
        })
        .catch((error) => {
          console.log('Error sending message:', error);
        });
        
      }, function (errorObject) {
        console.log("The read failed: " + errorObject.code);
      });

      

    });
