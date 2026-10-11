export interface Tag {
    id?: number
    tagName: string
    isOpen?: boolean
    isPrivate?: boolean
    pwd?: string
}

export interface Navigation {
    id?: number
    title: string
    des: string
    url: string
    icon: string
    tenant: string
    tagList?: Tag[]
}
