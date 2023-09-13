import firebase from 'firebase/app'
import firestore from 'firebase/firestore'

const firebaseConfig = {
    apiKey: "AIzaSyCD9_k-irHJfzVkXxB1Q-gcYcVX7BjVpe8",
    authDomain: "aston-interview-helper.firebaseapp.com",
    databaseURL: "https://aston-interview-helper.firebaseio.com",
    projectId: "aston-interview-helper",
    storageBucket: "",
    messagingSenderId: "730268252029",
    appId: "1:730268252029:web:94ac955f17e04028"
};

const firebaseApp = firebase.initializeApp(firebaseConfig);

export default firebaseApp.firestore()