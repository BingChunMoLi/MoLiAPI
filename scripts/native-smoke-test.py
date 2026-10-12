import argparse
import json
import time
import urllib.error
import urllib.parse
import urllib.request


def request(base_url, path, headers=None):
    http_request = urllib.request.Request(base_url.rstrip("/") + path, headers=headers or {})
    try:
        with urllib.request.urlopen(http_request, timeout=30) as response:
            return response.status, response.headers, response.read()
    except urllib.error.HTTPError as error:
        with error:
            return error.code, error.headers, error.read()


def check_json(base_url, path, headers=None, require_data=False):
    status, response_headers, body = request(base_url, path, headers)
    if status != 200 or "application/json" not in response_headers.get("Content-Type", ""):
        raise RuntimeError(f"{path}: expected HTTP 200 JSON, received {status}")
    payload = json.loads(body)
    if payload.get("code") != "00000":
        raise RuntimeError(f"{path}: application returned an error")
    if require_data and payload.get("data") is None:
        raise RuntimeError(f"{path}: expected populated data")
    print(f"PASS {path}")


def main():
    parser = argparse.ArgumentParser(description="Check a local MoLiAPI native or AOT application.")
    parser.add_argument("--base-url", default="http://127.0.0.1:8090")
    parser.add_argument("--startup-timeout", type=int, default=120)
    args = parser.parse_args()
    deadline = time.monotonic() + args.startup_timeout
    while True:
        try:
            check_json(args.base_url, "/version", require_data=True)
            break
        except (OSError, ValueError, RuntimeError):
            if time.monotonic() >= deadline:
                raise RuntimeError("Application did not become ready before the startup timeout")
            time.sleep(1)

    check_json(args.base_url, "/shici/random", require_data=True)
    check_json(args.base_url, "/yiyan/random", require_data=True)
    check_json(args.base_url, "/navigation", require_data=True)
    emoji_query = urllib.parse.urlencode({"emoji": ":smile:"})
    status, headers, body = request(args.base_url, "/emoji/unicode?" + emoji_query)
    charset = headers.get_content_charset() or "utf-8"
    if status != 200 or body.decode(charset) != "\U0001f604":
        raise RuntimeError("Emoji conversion did not return the expected Unicode text")
    print("PASS emoji conversion")
    check_json(args.base_url, "/address", {"X-Forwarded-For": "8.8.8.8"}, require_data=True)
    check_json(args.base_url, "/address", {"X-Forwarded-For": "2001:4860:4860::8888"}, require_data=True)

    status, headers, body = request(args.base_url, "/generate?text=native&width=64&height=64")
    if status != 200 or "image/jpeg" not in headers.get("Content-Type", "") or not body.startswith(b"\xff\xd8"):
        raise RuntimeError("QR generation did not return a JPEG image")
    print("PASS QR generation")

    status, headers, body = request(args.base_url, "/native-missing-route", {"Accept": "image/jpeg"})
    if status != 404 or "image/jpeg" not in headers.get("Content-Type", "") or not body.startswith(b"\xff\xd8"):
        raise RuntimeError("Missing route did not return the bundled 404 JPEG")
    print("PASS bundled error image")

    status, _, _ = request(args.base_url, "/system", {"Accept": "application/json"})
    if status != 401:
        raise RuntimeError(f"Unauthenticated configuration request returned {status} instead of 401")
    print("PASS configuration authentication")


if __name__ == "__main__":
    main()
