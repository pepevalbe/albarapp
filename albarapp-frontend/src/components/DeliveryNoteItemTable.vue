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
        </tr>
        <tr>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td></td>
          <td>Total: {{deliveryNoteTotal.toFixed(2)}} €</td>
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
    deliveryNoteTotal: Number
  }
};
</script>