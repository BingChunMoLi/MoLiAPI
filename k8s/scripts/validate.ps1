[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$kustomizeRoot = Join-Path $root 'kustomize/overlays'
$chart = Join-Path $root 'helm/moliapi'

function Invoke-CheckedCommand {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Command,
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments
    )

    $output = & $Command @Arguments 2>&1 | Out-String
    if ($LASTEXITCODE -ne 0) {
        throw "$Command $($Arguments -join ' ') failed:`n$output"
    }

    return $output
}

function Assert-Contains {
    param(
        [string]$Text,
        [string]$Pattern,
        [string]$Description
    )

    if ($Text -notmatch $Pattern) {
        throw "Missing $Description. Pattern: $Pattern"
    }
}

function Assert-NotContains {
    param(
        [string]$Text,
        [string]$Pattern,
        [string]$Description
    )

    if ($Text -match $Pattern) {
        throw "Unexpected $Description. Pattern: $Pattern"
    }
}

$minimalKustomize = Invoke-CheckedCommand 'kubectl' @(
    'kustomize', (Join-Path $kustomizeRoot 'minimal')
)
$fullKustomize = Invoke-CheckedCommand 'kubectl' @(
    'kustomize', (Join-Path $kustomizeRoot 'full')
)

if (Get-Command helm -ErrorAction SilentlyContinue) {
    Invoke-CheckedCommand 'helm' @('lint', $chart) | Out-Null
    $minimalHelm = Invoke-CheckedCommand 'helm' @(
        'template', 'moliapi', $chart, '-f', (Join-Path $chart 'values-minimal.yaml')
    )
    $fullHelm = Invoke-CheckedCommand 'helm' @(
        'template', 'moliapi', $chart, '-f', (Join-Path $chart 'values-full.yaml')
    )
} else {
    Write-Warning 'helm is not installed; Helm rendering checks were skipped.'
    $minimalHelm = ''
    $fullHelm = ''
}

$minimalOutputs = @($minimalKustomize, $minimalHelm) | Where-Object { $_ -ne '' }
$fullOutputs = @($fullKustomize, $fullHelm) | Where-Object { $_ -ne '' }

foreach ($output in $minimalOutputs) {
    Assert-Contains $output 'name: moliapi-mysql' 'MySQL service or workload'
    Assert-Contains $output 'name: moliapi-redis' 'Redis service or workload'
    Assert-Contains $output 'containerPort: 8090' 'MoLiAPI container port'
    Assert-Contains $output 'startupProbe:' 'startup probe'
    Assert-Contains $output 'readinessProbe:' 'readiness probe'
    Assert-Contains $output 'livenessProbe:' 'liveness probe'
    Assert-NotContains $output 'name: moliapi-prometheus' 'Prometheus in minimal mode'
    Assert-NotContains $output 'name: moliapi-grafana' 'Grafana in minimal mode'
}

foreach ($output in $fullOutputs) {
    Assert-Contains $output 'name: moliapi-prometheus' 'Prometheus in full mode'
    Assert-Contains $output 'name: moliapi-grafana' 'Grafana in full mode'
    Assert-Contains $output '/actuator/prometheus' 'MoLiAPI metrics path'
}

$allOutput = ($minimalOutputs + $fullOutputs) -join "`n"
Assert-NotContains $allOutput 'image: .*:latest' 'mutable latest image tag'
Assert-NotContains $allOutput '(?i)(loki|promtail|alloy|nginx-prometheus-exporter)' `
    'removed logging or exporter component'
Assert-NotContains $allOutput `
    '(?im)^\s+(MYSQL_ROOT_PASSWORD|MYSQL_PASSWORD|REDIS_PASSWORD|GF_SECURITY_ADMIN_PASSWORD):\s+[^\r\n]+$' `
    'inline plaintext credential'
Assert-Contains $allOutput 'resources:' 'container resource settings'
Assert-Contains $allOutput 'secretKeyRef:' 'Secret references'

Write-Output 'Kubernetes manifests rendered and passed parity checks.'