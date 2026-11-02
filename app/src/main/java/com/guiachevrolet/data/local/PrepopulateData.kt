package com.guiachevrolet.data.local

import com.guiachevrolet.data.local.entity.ErrorCodeEntity
import com.guiachevrolet.data.local.entity.Severity
import com.guiachevrolet.data.local.entity.VehicleEntity

object PrepopulateData {

    private val allModels = VehicleEntity.SUPPORTED_GM_MODELS

    val initialErrorCodes = listOf(
        ErrorCodeEntity(
            id = 1,
            codeNumber = 15,
            title = "Falha na luz de freio superior (Break Light)",
            description = "A lâmpada da terceira luz de freio (central elevada) está queimada ou com mau contato no chicote elétrico. Verifique e substitua a lâmpada.",
            severity = Severity.ALERTA,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 2,
            codeNumber = 16,
            title = "Luz de freio queimada ou falha no circuito",
            description = "Uma ou ambas as lâmpadas de freio traseiras estão queimadas ou há falha no interruptor do pedal de freio. Por segurança, substitua a lâmpada imediatamente.",
            severity = Severity.ALERTA,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 3,
            codeNumber = 18,
            title = "Falha no farol baixo esquerdo",
            description = "A lâmpada do farol baixo do lado do motorista está queimada ou desconectada. Verifique a lâmpada H4/H7 e o fusível correspondente.",
            severity = Severity.ALERTA,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 4,
            codeNumber = 20,
            title = "Falha no farol baixo direito",
            description = "A lâmpada do farol baixo do lado do passageiro está queimada ou desconectada. Verifique a lâmpada e o fusível correspondente.",
            severity = Severity.ALERTA,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 5,
            codeNumber = 21,
            title = "Falha na lanterna dianteira esquerda (Luz de posição)",
            description = "A lâmpada pingo (farolete/lanterna) dianteira esquerda apresenta falha. Substitua por uma lâmpada compatível (W5W) sem alterar a potência original.",
            severity = Severity.ALERTA,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 6,
            codeNumber = 22,
            title = "Falha na lanterna dianteira direita (Luz de posição)",
            description = "A lâmpada pingo (farolete/lanterna) dianteira direita está queimada ou com mau contato.",
            severity = Severity.ALERTA,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 7,
            codeNumber = 23,
            title = "Luz de ré queimada",
            description = "A lâmpada da marcha a ré está queimada ou o sensor de ré na caixa de câmbio apresenta mau contato.",
            severity = Severity.ALERTA,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 8,
            codeNumber = 24,
            title = "Luz da placa de licença queimada",
            description = "Uma ou mais lâmpadas de iluminação da placa traseira estão queimadas. Trafegar com a luz de placa apagada à noite pode gerar infração de trânsito.",
            severity = Severity.ALERTA,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 9,
            codeNumber = 25,
            title = "Seta dianteira esquerda com falha",
            description = "A lâmpada indicadora de direção (pisca) dianteira esquerda está queimada ou utilizando LED sem canceller.",
            severity = Severity.ALERTA,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 10,
            codeNumber = 26,
            title = "Seta traseira esquerda com falha",
            description = "A lâmpada indicadora de direção (pisca) traseira esquerda está queimada ou com mau contato no soquete da lanterna.",
            severity = Severity.ALERTA,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 11,
            codeNumber = 27,
            title = "Seta dianteira direita com falha",
            description = "A lâmpada indicadora de direção (pisca) dianteira direita está queimada.",
            severity = Severity.ALERTA,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 12,
            codeNumber = 28,
            title = "Seta traseira direita com falha",
            description = "A lâmpada indicadora de direção (pisca) traseira direita está queimada ou com oxidação no conector.",
            severity = Severity.ALERTA,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 13,
            codeNumber = 35,
            title = "Substituir bateria do controle remoto (Chave)",
            description = "A bateria da chave canivete ou presencial (geralmente modelo CR2032) está fraca. Substitua a bateria para evitar falha no destravamento ou partida.",
            severity = Severity.MANUTENCAO,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 14,
            codeNumber = 59,
            title = "Vidro elétrico requer reprogramação",
            description = "Após desconectar a bateria, o módulo de conforto perdeu a referência de fim de curso. Abaixe o vidro totalmente e segure o botão por 5 segundos; depois suba totalmente e segure por mais 5 segundos.",
            severity = Severity.MANUTENCAO,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 15,
            codeNumber = 65,
            title = "Tentativa de furto detectada",
            description = "O sistema de alarme antifurto do veículo foi disparado recentemente por abertura forçada ou sensor de ultrassom.",
            severity = Severity.ALERTA,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 16,
            codeNumber = 68,
            title = "Falha na assistência da direção elétrica (EPS)",
            description = "O sistema de direção assistida eletricamente detectou uma anomalia ou queda de tensão na bateria. A direção ficará mais pesada. Verifique a saúde da bateria e o alternador ou procure uma oficina.",
            severity = Severity.CRITICO,
            applicableModels = listOf("Onix", "Onix Plus", "Prisma", "Cobalt", "Spin", "Cruze", "Tracker", "Sonic", "S10", "Trailblazer", "Equinox", "Camaro")
        ),
        ErrorCodeEntity(
            id = 17,
            codeNumber = 79,
            title = "Nível baixo de óleo do motor — Complete o óleo",
            description = "O sensor detectou nível de óleo abaixo do mínimo seguro. Pare o veículo em local plano, aguarde o motor esfriar, verifique a vareta e complete com a especificação Dexos recomendada.",
            severity = Severity.CRITICO,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 18,
            codeNumber = 82,
            title = "Trocar óleo do motor em breve",
            description = "O sistema de vida útil do óleo calculou que o lubrificante atingiu o limite de uso. Agende a troca do óleo e do filtro o mais breve possível e realize o reset do aviso no painel.",
            severity = Severity.MANUTENCAO,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 19,
            codeNumber = 84,
            title = "Potência do motor reduzida (Modo de Emergência)",
            description = "O módulo de injeção limitou a potência do motor para proteger componentes internos (comum em falhas no corpo de borboleta/TBI, pedal do acelerador ou bobina de ignição). Dirija com cautela até uma oficina.",
            severity = Severity.CRITICO,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 20,
            codeNumber = 89,
            title = "Manutenção necessária em breve (Revisão / Arrefecimento)",
            description = "Indica falha em componentes monitorados pela carroceria ou motor sem acender a luz de injeção (muito comum em falha da válvula termostática eletrônica, sensor de temperatura do arrefecimento ou revisão programada).",
            severity = Severity.MANUTENCAO,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 21,
            codeNumber = 95,
            title = "Falha no sistema de Airbag / Pré-tensionadores",
            description = "Anomalia detectada no sistema de retenção suplementar (Airbag ou cintos de segurança). Frequentemente causado por mau contato no chicote embaixo do banco do motorista ou cinta do volante (hard disc). Os airbags podem não disparar em caso de colisão.",
            severity = Severity.CRITICO,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 22,
            codeNumber = 128,
            title = "Capô aberto",
            description = "O sensor do trinco do capô indica que o compartimento do motor não está completamente travado. Pare em local seguro e feche o capô firmemente.",
            severity = Severity.ALERTA,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 23,
            codeNumber = 136,
            title = "Falha no Assistente de Estacionamento (Sensor de Ré)",
            description = "Um dos sensores ultrassônicos do para-choque está sujo, danificado ou desconectado. Limpe o para-choque e verifique os conectores.",
            severity = Severity.MANUTENCAO,
            applicableModels = allModels
        ),
        ErrorCodeEntity(
            id = 24,
            codeNumber = 174,
            title = "Bateria do veículo com tensão baixa",
            description = "A tensão da bateria de 12V está abaixo do ideal ou o alternador não está carregando adequadamente. Teste a bateria e o sistema de carga.",
            severity = Severity.CRITICO,
            applicableModels = allModels
        )
    )
}
