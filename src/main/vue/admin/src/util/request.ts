import type {ResultVO} from '@/type/ResultVO'

const baseUrl = import.meta.env.VITE_API_BASE_URL

const buildUrl = (url: string) => `${baseUrl}${url}`

const request = async <T>(url: string, config: RequestInit): Promise<ResultVO<T>> => {
    const defaultConfig: RequestInit = {
        cache: 'no-cache',
        credentials: 'include'
    }
    const response = await fetch(buildUrl(url), {...defaultConfig, ...config})
    if (!response.ok) {
        throw new Error(`接口请求异常: ${response.status}`)
    }
    return response.json() as Promise<ResultVO<T>>
}

const withJsonBody = (data?: unknown, config?: RequestInit): RequestInit => ({
    ...config,
    body: JSON.stringify(data),
    headers: {
        'content-type': 'application/json',
        ...config?.headers
    }
})

export const get = async <T>(url: string, config?: RequestInit): Promise<T> => {
    return (await request<T>(url, {method: 'GET', ...config})).data
}

export const post = async <T>(url: string, data?: unknown, config?: RequestInit): Promise<ResultVO<T>> => {
    return request<T>(url, {
        method: 'POST',
        ...withJsonBody(data, config)
    })
}

export const put = async <T>(url: string, data?: unknown, config?: RequestInit): Promise<ResultVO<T>> => {
    return request<T>(url, {
        method: 'PUT',
        ...withJsonBody(data, config)
    })
}

export const remove = async <T>(url: string, config?: RequestInit): Promise<ResultVO<T>> => {
    return request<T>(url, {method: 'DELETE', ...config})
}
