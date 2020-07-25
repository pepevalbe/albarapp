import HttpClient from '@/services/HttpClient.js';

const LOGIN_URL = '/login';

export default {
  login(email, password) {
    var bodyFormData = new FormData();
    bodyFormData.set('username', email);
    bodyFormData.set('password', password);
    return HttpClient
      .post(`${LOGIN_URL}`, bodyFormData)
      .then(response => {
        return response.headers.authorization;
      })
      .catch((response) => {
        if (response?.response?.data?.errorCode == "003") {
          response.isError = true;
          return response;
        } else {
          return Promise.reject(response);
        }
      });
  }
}