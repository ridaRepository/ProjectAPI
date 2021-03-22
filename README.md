
 ##Test Case  
1.Should return notifications for the following countries: BR, AR

    User should able to send a GET request
    User should able to get 200 status code
    User should able to get response Payload/Body
    User should able to see countries each(BR OR AR) notification
    
2.perPage value should correspond to the number of notifications retrieved

    User should able to send GET request 
    user should able to get 200 status code
    User shoud able to get response Payload/Body
    User able to get perPage value on Body 
    User able to get count number of notifications
    User able to assert perPage and num. of notif are equals

3.Content of notifications should be a xml encoded on Base64

    User should able to send GET request
    user should able to get 200 status code 
    user should able to get response body and manipulate
    user able to verify notifications are xml and encoded with Base64

4.NotificationId should be a valid GUID

    User should able to send GET request
    user should able to get 200 status code 
    user should able to get response body 
    user should see NotificationId is valid GUID
    
5.NotificationId should correspond to ID inside content xml document
    
    User should able to send GET request
    user should able to get 200 status code 
    user should able to get response body
    user should be able to verify ID same with NotificationID   

6.200 notifications should have "Document Authorized" on StatusReason and "Document authorized successfully" on Text fields inside content xml document

    User should able to send GET request
    user should able to get 200 status code 
    user should able to get response body
    user should be able to see content 200
    user should verify statusReason equals "Document Authorized"
    user shoudl verify text equals "Document authorized successfully"
    


7.400 notifications should have "Document Rejected" on StatusReason and "Document was rejected by tax authority" on Text fields inside content xml document

      User should able to send GET request
      user should able to get 200 status code 
      user should able to get response body
      user should be able to see content 400
      user should verify statusReason equals "Document Rejected"
      user shoudl verify text equals "Document was rejected by tax authority"