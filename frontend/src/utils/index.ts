export function formatTime(time: string) {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

export function formatDate(date: string) {
  if (!date) return ''
  return date.substring(0, 10)
}
