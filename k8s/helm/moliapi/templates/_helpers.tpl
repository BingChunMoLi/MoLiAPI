{{- define "moliapi.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "moliapi.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- include "moliapi.name" . -}}
{{- end -}}
{{- end -}}

{{- define "moliapi.labels" -}}
helm.sh/chart: {{ printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/part-of: moliapi
{{- end -}}

{{- define "moliapi.selectorLabels" -}}
app.kubernetes.io/name: {{ include "moliapi.fullname" . }}
{{- end -}}

{{- define "moliapi.secretName" -}}
{{- required "必须设置 existingSecret，并在安装 Chart 前创建该 Secret" .Values.existingSecret -}}
{{- end -}}

{{- define "moliapi.mysqlHost" -}}
{{- if .Values.mysql.enabled -}}
{{- printf "%s-mysql" (include "moliapi.fullname" .) -}}
{{- else -}}
{{- required "mysql.enabled=false 时必须设置 external.mysql.host" .Values.external.mysql.host -}}
{{- end -}}
{{- end -}}

{{- define "moliapi.redisHost" -}}
{{- if .Values.redis.enabled -}}
{{- printf "%s-redis" (include "moliapi.fullname" .) -}}
{{- else -}}
{{- required "redis.enabled=false 时必须设置 external.redis.host" .Values.external.redis.host -}}
{{- end -}}
{{- end -}}

{{- define "moliapi.prometheusConfig" -}}
global:
    scrape_interval: 15s
    evaluation_interval: 15s
scrape_configs:
    - job_name: prometheus
      static_configs:
          - targets:
                - localhost:9090
    - job_name: moliapi
      metrics_path: /actuator/prometheus
      static_configs:
          - targets:
                - {{ include "moliapi.fullname" . }}:8090
{{- end -}}

{{- define "moliapi.grafanaDatasource" -}}
apiVersion: 1
datasources:
    - name: Prometheus
      type: prometheus
      access: proxy
      url: http://{{ include "moliapi.fullname" . }}-prometheus:9090
      isDefault: true
      editable: false
{{- end -}}