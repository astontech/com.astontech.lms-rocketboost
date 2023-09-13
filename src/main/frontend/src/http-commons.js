import axios from 'axios'

let baseURL;
let proxyURL;

if (!process.env.NODE_ENV || process.env.NODE_ENV === 'development' || window.location.hostname === "localhost") {
    baseURL = 'http://localhost:8080';
    proxyURL = 'https://localhost:8081';
}
else {
    baseURL = 'https://rocketboost.astontech.com';
    proxyURL = 'https://rocketboost.astontech.com';
}

export const AXIOS = axios.create({
    baseURL: baseURL,
    headers: {
        'Access-Control-Allow-Origin': proxyURL
    }
})