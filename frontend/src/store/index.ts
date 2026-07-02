import { createStore } from 'vuex'
import defaultAvatar from '@/assets/images/default-avatar.png'

function getInitialAvatar() {
  const saved = sessionStorage.getItem('userAvatar')
  if (saved) return saved
  return defaultAvatar
}

export default createStore({
  state: {
    userPic: getInitialAvatar(),
    isPlay: false,
    url: '',
    id: '',
    breadcrumbList: []
  },
  getters: {
    userPic: state => state.userPic,
    isPlay: state => state.isPlay,
    url: state => state.url,
    id: state => state.id,
    breadcrumbList: state => state.breadcrumbList
  },
  mutations: {
    setUserPic: (state, userPic) => { state.userPic = userPic },
    setIsPlay: (state, isPlay) => { state.isPlay = isPlay },
    setUrl: (state, url) => { state.url = url },
    setId: (state, id) => { state.id = id },
    setBreadcrumbList: (state, breadcrumbList) => { state.breadcrumbList = breadcrumbList }
  }
})
