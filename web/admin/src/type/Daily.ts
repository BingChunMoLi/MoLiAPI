export interface Daily {
  id?: number
  url: string
  tenant?: string
  type: number
  createTime: string
}

export interface DailyMap {
  [key: string]: Daily[]
}
