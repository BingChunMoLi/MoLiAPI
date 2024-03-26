const baseUrl = import.meta.env.VITE_API_BASE_URL;

const request = async (url: string, config: RequestInit) => {
    const defaultConfig: RequestInit = {
        cache: 'no-cache'
    }
    const newConfig = {...config, ...defaultConfig}
    return fetch(baseUrl + url, newConfig)
        .then((res: Response) => {
            if (!res.ok) {
                // 服务器异常返回
                throw Error('接口请求异常');
            }
            return res.json();
        })
        .catch((error: any) => {
            return Promise.reject(error);
        });
};

export const get = async (url: string, config?: RequestInit) => {
    return (await request(url, {method: 'GET', ...config})).data
};

// POST请求
export const post = (url: string, data?: any, config?: RequestInit) => {
    return request(url, {
        body: JSON.stringify(data),
        headers: {
            'content-type': 'application/json',
        },
        method: 'POST',
        ...config
    });
};
