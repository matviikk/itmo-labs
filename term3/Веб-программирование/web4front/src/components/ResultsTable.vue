<template>
  <div class="custom-table-container">
    <table class="custom-table">
      <thead>
      <tr>
        <th>X</th>
        <th>Y</th>
        <th>R</th>
        <th>Hit</th>
        <th>Check Time</th>
        <th>Execution Time (ns)</th>
        <th>Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="point in points" :key="point.id">
        <td>{{ point.x }}</td>
        <td>{{ point.y }}</td>
        <td>{{ point.radius }}</td>
        <td>
            <span :class="['tag', point.hit ? 'success' : 'danger']">
              {{ point.hit ? 'Hit' : 'Miss' }}
            </span>
        </td>
        <td>{{ formatDateTime(point.checkTime) }}</td>
        <td>{{ point.executionTime }}</td>
        <td>
          <button @click="$emit('delete-point', point.id)"
                  class="ui-button-danger ui-button-rounded ui-button-sm custom-button">
            <i class="pi pi-trash"></i>
          </button>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  name: 'ResultsTable',
  props: {
    points: {
      type: Array,
      required: true
    }
  },
  methods: {
    formatDateTime(datetime) {
      return new Date(datetime).toLocaleString()
    }
  }
}
</script>