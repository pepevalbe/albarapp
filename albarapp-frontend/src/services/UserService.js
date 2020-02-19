import HttpClient from '@/services/HttpClient.js';

const USER_RESOURCE_NAME = '/hateoas/users';
const USER_PROFILE_URL = 'api/profile';
const SEND_INVITATION_URL = '/api/send-invitation';
const CREATE_USER_URL = '/user-creation';

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
        return response.data._embedded.users;
      })
      .catch(() => {
        alert("Ha ocurrido un error recuperando los usuarios");
      });
  },

  sendInvitation(email, role) {
    var invitation = { "email": email, "role": role };
    return HttpClient.post(SEND_INVITATION_URL, invitation)
      .then(() => {
        alert("Invitación enviada");
      })
  },

  createUser(invitation, name, surname, password) {
    var registration = { "token": invitation, "name": name, "surname": surname, "password": password };
    return HttpClient.post(CREATE_USER_URL, registration)
      .then(() => {
        alert("Usuario creado. Ya puede entrar utilizando su email y contraseña");
      })
  },
};