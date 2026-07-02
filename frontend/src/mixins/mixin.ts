import { RouterName } from "@/enums"
import { useRouter } from "vue-router"

export default function useMixin() {
  const router = useRouter()

  function routerManager(name: string, query?: object) {
    router.push({ path: name, query })
  }

  return { routerManager, RouterName }
}
