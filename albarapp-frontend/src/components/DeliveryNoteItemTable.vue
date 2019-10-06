<template>
  <v-simple-table>
    <template v-slot:default>
      <thead>
        <tr>
          <th class="text-left">Cantidad</th>
          <th class="text-left">Producto</th>
          <th class="text-left">Precio</th>
          <th class="text-left">Total bruto</th>
          <th class="text-left">Tipo impositivo</th>
          <th class="text-left">Total neto</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in deliveryNoteItems" :key="item.itemNumber">
          <td>{{ item.quantity }}</td>
          <td>{{ item.productName }}</td>
          <td>{{ item.price }}</td>
          <td>{{ item.gross.toFixed(2) }} €</td>
          <td>{{ item.taxRate }} %</td>
          <td>{{ item.net.toFixed(2) }} €</td>
          <td justify="center">
            <div class="text-xs-center">
              <v-btn
                class="ma-2"
                justify="center"
                color="red"
                dark
                @click="removeDeliveryNoteItem(item)"
              >
                <v-icon dark>mdi-delete</v-icon>
              </v-btn>
            </div>
          </td>
        </tr>
        <tr>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td>Total: {{deliveryNoteTotal.value.toFixed(2)}} €</td>
        </tr>
      </tbody>
    </template>
  </v-simple-table>
</template>

<script>
export default {
  name: "DeliveryNoteItemTable",
  props: {
    deliveryNoteItems: Array,
    deliveryNoteTotal: Object
  },
  methods: {
    removeDeliveryNoteItem(deliveryNoteItem) {
      this.deliveryNoteItems.splice(
        this.deliveryNoteItems.findIndex(item => item === deliveryNoteItem),
        1
      );
      this.deliveryNoteTotal.value -= deliveryNoteItem.net;
    }
  }
};
</script>