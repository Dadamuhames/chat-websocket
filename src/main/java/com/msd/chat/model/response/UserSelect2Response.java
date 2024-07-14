package com.msd.chat.model.response;

import lombok.Builder;

@Builder
public record UserSelect2Response(Long id, String text) implements UserResponseInterface {}
