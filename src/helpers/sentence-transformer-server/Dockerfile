# syntax=docker/dockerfile:1
FROM python:3.14-alpine AS builder

WORKDIR /app

# Install build deps
RUN apk add --no-cache build-base

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Runtime stage: minimal image, no build tools
FROM python:3.14-alpine AS runtime

WORKDIR /app

# Copy only runtime needs from builder
COPY --from=builder /usr/local/lib/python3.14/site-packages /usr/local/lib/python3.14/site-packages
COPY --from=builder /usr/local/bin /usr/local/bin
COPY . .

EXPOSE 5000

CMD ["python", "main.py"]
