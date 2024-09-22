import './assets/main.css'

import {createApp} from 'vue'
import {createPinia} from 'pinia'

import App from './App.vue'
import router from './router'
import 'normalize.css'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')

window.onerror = function (message, source, lineno, colno, error) {
    console.error('Error caught by window.onerror:', message, source, lineno, colno, error);
    return true;
}

app.config.errorHandler = function (err, vm, info) {
    console.error('Error:', err, info);
    // 全局处理错误并提示
};
