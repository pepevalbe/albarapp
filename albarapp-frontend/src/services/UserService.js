import HttpClient from '@/services/HttpClient.js';

const USER_RESOURCE_NAME = '/api/users';
const USER_PROFILE_URL = '/api/profile';
const SEND_INVITATION_URL = '/api/send-invitation';
const CREATE_USER_URL = '/user-creation';
const DELETE_USER_URL = 'api/user-delete';

export default {
  getProfile() {
    return HttpClient.get(USER_PROFILE_URL)
      .then(response => {
        return response.data;
      });
  },

  getAllUsers() {
    return HttpClient.get(USER_RESOURCE_NAME)
      .then(response => {
        return response.data.map(user => {
          user.rolesPlain = user.roles.join(', ')
          return user
        })
      });
  },

  delete(id) {
    return HttpClient.delete(`${DELETE_USER_URL}/${id}`);
  },

  sendInvitation(email, role) {
    var invitation = { "email": email, "role": role };
    return HttpClient.post(SEND_INVITATION_URL, invitation);
  },

  createUser(invitation, name, surname, password) {
    var registration = { "token": invitation, "name": name, "surname": surname, "password": password };
    return HttpClient.post(CREATE_USER_URL, registration);
  },
};