find ~ -type f -name "*.java" -exec sh -c '
  f="{}"
  iconv --from-code=ISO-8859-1 --to-code=UTF-8 -- "$f" > "$f".tmp
  mv -v -- "$f".tmp "$f"
  ' \;
