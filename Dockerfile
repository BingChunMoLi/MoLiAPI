FROM golang:latest as builder
LABEL authors="BingChunMoLi"
WORKDIR /build
COPY . .
RUN CGO_ENABLED=0 go build -ldflags="-s -w" -o moliapi main.go

FROM gcr.io/distroless/static-debian12
WORKDIR /app
COPY --from=builder /build/moliapi .
COPY --from=builder /build/yiyan ./yiyan/
ENTRYPOINT ["./moliapi"]