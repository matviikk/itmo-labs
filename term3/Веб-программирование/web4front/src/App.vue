<template>
  <router-view></router-view>
</template>

<script>
import axios from 'axios'

// axios.defaults.baseURL = 'http://localhost:4433/web4-1.0';
axios.defaults.baseURL = 'http://localhost:8080';

export default {
  name: 'App'
}
</script>

<style>
@import './assets/css/styles.css';
</style>