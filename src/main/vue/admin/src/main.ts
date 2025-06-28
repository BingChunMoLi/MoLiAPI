import './assets/main.css'
import 'aplayer-ts/src/css/base.css'
import 'aplayer-ts/src/css/fixed.css'
import {createApp} from 'vue'
import {createPinia} from 'pinia'

import App from './App.vue'
import router from './router'
import 'normalize.css'
import APlayer, {addMusicPlugin, APlayerFixedModePlugin, removeMusicPlugin} from 'aplayer-ts'
import {get} from './util/request'
import type {Song} from './type/Song'

const app = createApp(App)

const songs: Array<Song> = await get('music/1')

const aplayer = APlayer()
.use(APlayerFixedModePlugin)
.use(addMusicPlugin)
.use(removeMusicPlugin)
.init({
    container: document.getElementById("aplayer") as HTMLElement | undefined,
    volume: 0.4,
    listFolded: false,
    audio: {
        name: '正在加载...',
        artist: '正在加载...',
        lrc: 'data:,loading...',
    }
});
(async () => {
    const audios = songs.map(v => ({
        name: v.name,
        url: 'https://music.163.com/song/media/outer/url?id=' + v.thirdId,
        cover: v.picUrl,
        artist: v.nickname,
    })) ?? []
    aplayer.list.remove(0)
    aplayer.list.add(audios)
})()


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
