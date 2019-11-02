import HttpClient from '@/services/HttpClient.js';

const USER_RESOURCE_NAME = '/hateoas/users';
const USER_PROFILE_URL = 'api/profile';
const SEND_INVITATION_URL = '/api/send-invitation';
const CREATE_USER_URL = '/public/user-creation';

export default {
  getProfile() {
    return HttpClient.get(USER_PROFILE_URL)
      .then(response => {
        return response.data;
      })
      .catch(() => {
        alert("Ha ocurrido un error recuperando el perfil");
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
      .catch(() => {
        alert("Ha ocurrido un error enviando la invitación");
      });
  },

  createUser(invitation, name, surname, password) {
    var registration = { "token": invitation, "name": name, "surname": surname, "password": password };
    return HttpClient.post(CREATE_USER_URL, registration)
      .then(() => {
        alert("Usuario creado. Ya puede entrar utilizando su email y contraseña");
      })
      .catch(() => {
        alert("Ha ocurrido un error creando el usuario");
      });
  },
};