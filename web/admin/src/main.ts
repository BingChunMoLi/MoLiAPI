import './assets/main.css'
import 'aplayer-ts/src/css/base.css'
import 'aplayer-ts/src/css/fixed.css'
import 'normalize.css'

import APlayer, {addMusicPlugin, APlayerFixedModePlugin, removeMusicPlugin} from 'aplayer-ts'
import {createPinia} from 'pinia'
import {createApp} from 'vue'

import App from './App.vue'
import router from './router'
import type {Song} from './type/Song'
import {get} from './util/request'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')

const initPlayer = async () => {
    try {
        const songs: Array<Song> = await get<Song[]>('music/1')
        if (!songs.length) {
            return
        }
        const aplayer = APlayer()
            .use(APlayerFixedModePlugin)
            .use(addMusicPlugin)
            .use(removeMusicPlugin)
            .init({
                container: document.getElementById('aplayer') as HTMLElement | undefined,
                volume: 0.4,
                listFolded: false,
                audio: {
                    name: songs[0].name,
                    url: 'https://music.163.com/song/media/outer/url?id=' + songs[0].thirdId,
                    cover: songs[0].picUrl,
                    artist: songs[0].nickname
                }
            })
        const audios = songs.map(v => ({
            name: v.name,
            url: 'https://music.163.com/song/media/outer/url?id=' + v.thirdId,
            cover: v.picUrl,
            artist: v.nickname
        }))
        aplayer.list.add(audios.slice(1))
    } catch (error) {
        console.error('Music player init failed:', error)
    }
}

initPlayer()

window.onerror = function (message, source, lineno, colno, error) {
    console.error('Error caught by window.onerror:', message, source, lineno, colno, error)
    return true
}

app.config.errorHandler = function (err, vm, info) {
    console.error('Error:', err, info)
}
