package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LabelService {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    public List<LabelDTO> getAll() {
        return labelRepository.findAll()
                .stream().map(labelMapper::map)
                .toList();
    }
    public LabelDTO findTask(Long id) {
        Label label = labelRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not Found: " + id));
        return labelMapper.map(label);
    }
    public LabelDTO createLabel(LabelCreateDTO dto) {
        Label label = labelMapper.map(dto);
        labelRepository.save(label);
        return labelMapper.map(label);
    }
}
