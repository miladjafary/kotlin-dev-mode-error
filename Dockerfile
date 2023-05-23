FROM alpine

RUN adduser -S payment-service

RUN apk update \
	&& apk add --no-cache tzdata

WORKDIR /app

COPY ./build/main /app/payment-service

USER payment-service

CMD ["./payment-service"]
