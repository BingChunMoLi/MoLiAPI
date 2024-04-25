FROM golang:latest as builder
LABEL authors="BingChunMoLi"
WORKDIR /build
COPY . .
RUN go build -o moliapi main.go

FROM gcr.io/distroless/static-debian12
WORKDIR /app
COPY --from=builder /build/moliapi /app/moliapi
ENTRYPOINT ["./moliapi"]