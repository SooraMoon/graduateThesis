import {createStore} from "vuex"

export default createStore({
    state: {
        cachePath:[],
        bookShelfList:[],
        bookLookLists:[],
        downloadLists:[],
        historyList:[],
        reqArr: {
            isDetail: {
                isHome: true,
                isBoyComic: false,
                isGirlComic: false
            },
            isSearch: false,
        },
        login:false,
        vip:false,
        gift:false,
        guan:false
    },
    getters: {
        getReqArr (state) {
            return state.reqArr
        }
    },
    actions: {

    },
    mutations: {
        NEW_CACHE_PATH (state, value) {
            state.cachePath.push(value)
        },
        UPDATE_REQARR_ISHOME (state, value) {
            state.reqArr.isDetail.isHome = value
        },
        UPDATE_REQARR_ISBOYCOMIC (state, value) {
            state.reqArr.isDetail.isBoyComic = value
        },
        UPDATE_REQARR_ISGIRLCOMIC (state, value) {
            state.reqArr.isDetail.isGirlComic = value
        },
        UPDATE_REQARR_ISSEARCH (state, value) {
            state.reqArr.isSearch = value
        }
    }
})
