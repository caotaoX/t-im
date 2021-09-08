
const setting = {
    state: {
        voice: true,
        browserJitter: true
    },

    mutations: {
        SET_VOICE: (state, voice) => {
            state.voice = voice
        },
        SET_BROWSER: (state, browserJitter) => {
            state.browserJitter = browserJitter
        }
    },

    actions: {
        SetVoice({ commit }, voice) {
            return new Promise(() => {
                commit('SET_VOICE',voice)
            })
        },
        SetBrowser({ commit }, browserJitter) {
            return new Promise(() => {
                commit('SET_BROWSER',browserJitter)
            })
        },
    }
}

export default setting
