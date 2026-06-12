<template>
  <div class="custom-container">
    <Header />
    <div class="main-content">
      <div class="coordinate-system mb-4">
        <canvas id="coordinateCanvas" width="400" height="400" style="border: 1px solid #ccc;"></canvas>
      </div>

      <div class="coordinate-panel">
        <div class="grid">
          <div class="col-12 md:col-4">
            <div class="input-group">
              <label class="input-label">X coordinate:</label>
              <div class="checkbox-group">
                <label v-for="value in xValues" :key="value" class="checkbox-label">
                  <input
                      type="checkbox"
                      :value="value"
                      v-model="selectedX"
                      @change="handleXChange(value)"
                  >
                  {{ value }}
                </label>
              </div>
              <span v-if="errors.x" class="error-text">{{ errors.x }}</span>
            </div>
          </div>
          <div class="col-12 md:col-4">
            <div class="input-group">
              <label class="input-label">Y coordinate:</label>
              <input
                  type="text"
                  v-model="y"
                  class="w-full"
                  :class="{ 'error': errors.y }"
                  @input="validateY"
              />
              <span v-if="errors.y" class="error-text">{{ errors.y }}</span>
            </div>
          </div>
          <div class="col-12 md:col-4">
            <div class="input-group">
              <label class="input-label">Radius:</label>
              <div class="checkbox-group">
                <label v-for="value in rValues" :key="value" class="checkbox-label">
                  <input
                      type="checkbox"
                      :value="value"
                      v-model="selectedR"
                      @change="handleRChange(value)"
                  >
                  {{ value }}
                </label>
              </div>
              <span v-if="errors.r" class="error-text">{{ errors.r }}</span>
            </div>
          </div>
        </div>

        <div class="flex justify-content-center gap-3 p-3">
          <button @click="checkPoint" class="ui-button-success custom-button" :disabled="!isFormValid">
            Check Point
          </button>
          <button @click="clearPoints" class="ui-button-danger custom-button">
            Clear All Points
          </button>
          <button @click="logout" class="ui-button-secondary custom-button">
            Log Out
          </button>
        </div>
      </div>

      <ResultsTable :points="points" @delete-point="deletePoint" />
    </div>
  </div>
</template>

<script>
import Header from '../components/Header.vue'
import ResultsTable from '../components/ResultsTable.vue'
import { Graph } from '../utils/graph'
import axiosInstance from '../utils/axiosInstance'

export default {
  name: 'Main',
  components: {
    Header,
    ResultsTable
  },
  data() {
    return {
      selectedX: [],
      x: null,
      y: '',
      selectedR: [],
      radius: null,
      points: [],
      graph: null,
      errors: {
        x: '',
        y: '',
        r: ''
      },
      xValues: [-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2],
      rValues: [-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2]
    }
  },
  computed: {
    isFormValid() {
      return this.x !== null &&
          this.y !== '' &&
          this.radius !== null &&
          !this.errors.x &&
          !this.errors.y &&
          !this.errors.r;
    }
  },
  mounted() {
    this.loadPoints()
    this.graph = new Graph(this)
  },
  methods: {
    validateY() {
      this.errors.y = ''
      if (this.y === '') {
        this.errors.y = 'Y coordinate is required'
        return false
      }
      const yNum = Number(this.y)
      if (isNaN(yNum)) {
        this.errors.y = 'Y must be a number'
        return false
      }
      if (yNum < -5 || yNum > 5) {
        this.errors.y = 'Y must be between -5 and 5'
        return false
      }
      return true
    },
    handleXChange(value) {
      this.selectedX = [value]
      this.x = value
      this.errors.x = ''
    },
    handleRChange(value) {
      this.selectedR = [value]
      this.radius = value
      this.errors.r = ''
      this.graph?.handleRadiusChange(value)
    },
    async loadPoints() {
      try {
        const response = await axiosInstance.get('/api/points')
        this.points = response.data
        this.graph?.drawPoints()
      } catch (error) {
        if (error.response?.status === 401) {
          this.$router.push('/')
        }
      }
    },
    async checkPoint() {
      if (!this.isFormValid) {
        if (!this.x) this.errors.x = 'X coordinate is required'
        if (!this.validateY()) this.errors.y = 'Y coordinate is required'
        if (!this.radius) this.errors.r = 'Radius is required'
        return
      }

      try {
        const response = await axiosInstance.post('/api/points', null, {
          params: {
            x: this.x,
            y: this.y,
            radius: this.radius
          }
        })
        this.points.push(response.data)
        this.graph?.drawPoints()
      } catch (error) {
        if (error.response?.status === 401) {
          this.$router.push('/')
        }
      }
    },
    async deletePoint(id) {
      try {
        await axiosInstance.delete(`/api/points/${id}`)
        this.points = this.points.filter(point => point.id !== id)
        this.graph?.drawPoints()
      } catch (error) {
        if (error.response?.status === 401) {
          this.$router.push('/')
        }
      }
    },
    async clearPoints() {
      try {
        await axiosInstance.delete('/api/points')
        this.points = []
        this.graph?.drawPoints()
      } catch (error) {
        if (error.response?.status === 401) {
          this.$router.push('/')
        }
      }
    },
    logout() {
      localStorage.removeItem('token')
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  cursor: pointer;
}

.error-text {
  color: #dc3545;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

input.error {
  border-color: #dc3545;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>