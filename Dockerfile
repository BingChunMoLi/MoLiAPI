FROM node:22-alpine AS frontend
WORKDIR /build
COPY web/admin/package.json web/admin/yarn.lock ./
RUN corepack enable && yarn install --frozen-lockfile
COPY web/admin/ ./
RUN yarn build

FROM golang:1.25 AS builder
WORKDIR /build
COPY go.mod go.sum ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 go build -trimpath -ldflags="-s -w" -o moliapi .

FROM gcr.io/distroless/static-debian12
WORKDIR /app
COPY --from=builder /build/moliapi .
COPY --from=builder /build/yiyan ./yiyan/
COPY --from=builder /build/ui ./ui/
COPY --from=frontend /build/dist ./web/admin/dist/
ENV PORT=8080 TZ=Asia/Shanghai
EXPOSE 8080
ENTRYPOINT ["./moliapi"]
